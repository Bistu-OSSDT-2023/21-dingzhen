#include <iostream>
#include <opencv2/opencv.hpp>
#include <filesystem>
#include <fstream>
#include <string>
#include <tesseract/baseapi.h>
#include <Windows.h>

cv::Mat tiquchepai(cv::Mat image);//用于提取车牌图片的函数
std::string tiquhaoma(cv::Mat licensePlate);//用于提取车牌字符图片并传递给Tesseract
std::string compare(cv::Mat image);//使用Tesseract进行文字识别
static int a =0;
bool compareRect(const cv::Rect& rect1, const cv::Rect& rect2);//用于对图片进行排序

int main()
{
    std::ifstream infile("input.txt");
    if (!infile.is_open())
    {
        std::cerr << "Failed to open input.txt file." << std::endl;
        return -1;
    }

    std::string imageFilePath;
    std::getline(infile, imageFilePath);
    infile.close();

    // Read the image
    cv::Mat image = cv::imread(imageFilePath);
    if (image.empty())
    {
        std::cerr << "Failed to read image." << std::endl;
        return -1;
    }

    // Rest of the code remains the same...

    // Display result image
    cv::Mat License = tiquchepai(image);
    std::string name;
    name = tiquhaoma(License);
    // Write result to file
    std::ofstream outfile("result.txt");
    if (outfile.is_open())
    {
        outfile << name << std::endl;
        outfile.close();
        std::cout << "数据已成功写入文件。" << std::endl;
    }
    else
    {
        std::cout << "无法打开文件。" << std::endl;
    }
    
    return 0;
}

cv::Mat tiquchepai(cv::Mat image) {
    // 图像预处理
    cv::Mat grayImage;
    cv::cvtColor(image, grayImage, cv::COLOR_BGR2GRAY);//对图像进行灰度处理
    cv::GaussianBlur(grayImage, grayImage, cv::Size(3, 3), 0);//对图像进行高斯模糊处理

    // 边缘检测
    cv::Mat edges;
    cv::Canny(grayImage, edges, 100, 200);//对图像进行边缘检测

    // 轮廓检测
    std::vector<std::vector<cv::Point>> contours;//创建一个point的动态数组
    cv::findContours(edges, contours, cv::RETR_EXTERNAL, cv::CHAIN_APPROX_SIMPLE);//对边缘检测后的图所生成的矩形端点进行储存

    // 遍历轮廓
    for (const auto& contour : contours)
    {
        cv::Rect boundingRect = cv::boundingRect(contour);//创建矩形对象来储存点
        double aspectRatio = static_cast<double>(boundingRect.width) / boundingRect.height;
        // 根据颜色进行筛选
        cv::Mat roi = image(boundingRect);
        cv::Mat hsvRoi;
        cv::cvtColor(roi, hsvRoi, cv::COLOR_BGR2HSV);

        // 设置颜色阈值（在此示例中，假设要筛选蓝色）
        cv::Scalar lowerBound(90, 50, 50);  // 蓝色下界
        cv::Scalar upperBound(130, 255, 255);  // 蓝色上界
        cv::Mat mask;
        cv::inRange(hsvRoi, lowerBound, upperBound, mask);

        // 计算颜色像素比例
        double colorPixelRatio = static_cast<double>(cv::countNonZero(mask)) / (mask.rows * mask.cols);
        // 根据宽高比和面积进行车牌区域筛选
        if (aspectRatio > 2.5 && aspectRatio < 3.5 && boundingRect.area() > 1000 && colorPixelRatio > 0.5)
        {
            // 在图像上绘制车牌区域
            cv::rectangle(image, boundingRect, cv::Scalar(0, 255, 0), 2);

            // 提取车牌图像
            cv::Mat licensePlate = image(boundingRect);
            cv::Mat cutimg = image(boundingRect);
            return cutimg;
        }

    }
}
std::string tiquhaoma(cv::Mat licensePlate) {
    static std::string name;
    cv::Mat grayPlate;
    cv::cvtColor(licensePlate, grayPlate, cv::COLOR_BGR2GRAY);
    cv::GaussianBlur(grayPlate, grayPlate, cv::Size(3, 3), 0);
    cv::threshold(grayPlate, grayPlate, 100, 255, cv::THRESH_BINARY | cv::THRESH_OTSU);
    // 边缘检测
    cv::Mat edges;
    cv::Canny(grayPlate, edges, 100, 200);//对图像进行边缘检测

    // 寻找边缘的轮廓
    std::vector<std::vector<cv::Point>> contours;
    cv::findContours(edges, contours, cv::RETR_EXTERNAL, cv::CHAIN_APPROX_NONE);

    // 填充边缘内部为白色
    cv::Mat filledEdges = cv::Mat::zeros(edges.size(), CV_8UC1);
    cv::drawContours(filledEdges, contours, -1, cv::Scalar(255), cv::FILLED);
    cv::Mat kernel = cv::getStructuringElement(cv::MORPH_RECT, cv::Size(4, 4));//getStructuringElement返回值定义内核矩阵
    cv::erode(filledEdges, filledEdges, kernel);//erode函数直接进行腐蚀操作

    kernel = cv::getStructuringElement(cv::MORPH_RECT, cv::Size(9, 8));//getStructuringElement返回值定义内核矩阵
    cv::dilate(filledEdges, filledEdges, kernel);//dilate函数直接进行膨胀操作
    
    cv::findContours(filledEdges, contours, cv::RETR_EXTERNAL, cv::CHAIN_APPROX_NONE);
    // 查找满足条件的区域并切割输出
    cv::Mat output = licensePlate.clone();
    std::vector<cv::Rect> suitRect;
    for (const auto& contour : contours) {
        // 计算轮廓的边界框
        cv::Rect boundingRect = cv::boundingRect(contour);

        // 检查区域的长宽比是否接近1:2
        double aspectRatio = static_cast<double>(boundingRect.width) / boundingRect.height;
        if (aspectRatio >= 0.3 && aspectRatio <= 0.7){
            // 在输出图像上绘制边界框
            cv::rectangle(output, boundingRect, cv::Scalar(0, 255, 0), 2);
            suitRect.push_back(boundingRect);
        }
    }
    std::string namee;
    std::sort(suitRect.begin(), suitRect.end(), compareRect);//按x轴排序
    for (const auto& rect:suitRect)
    {
        cv::rectangle(output, rect,cv::Scalar(0, 255, 0), 2);
        cv::Mat croppedRegion = licensePlate(rect);
         namee+=compare(croppedRegion);
    }
    return namee;
    
}
bool compareRect(const cv::Rect& rect1, const cv::Rect& rect2) {
    return rect1.x < rect2.x;
}
std::string compare(cv::Mat inputImage) {
   
//使用Tesseract-OCR进行文字识别，汉字识别率不高
    tesseract::TessBaseAPI ocr;
    cv::Mat proimg = inputImage;
    cv::cvtColor(inputImage, proimg, cv::COLOR_BGR2GRAY);  // 对图像进行灰度处理
    cv::GaussianBlur(proimg, proimg, cv::Size(3, 3), 0);
    cv::threshold(proimg, proimg, 155, 255, cv::THRESH_BINARY);//| cv::THRESH_OTSU

    if (a == 0)
    {
        ocr.Init("tessdata", "chi");
        a++;
        ocr.SetImage(proimg.data, proimg.cols, proimg.rows, 1, proimg.cols);
        char* text = ocr.GetUTF8Text();
        std::string result(text);
        return result;
    }
    else {
        ocr.Init("tessdata", "eng");
        ocr.SetImage(proimg.data, proimg.cols, proimg.rows, 1, proimg.cols);
        char* text = ocr.GetUTF8Text();
        std::string result(text);
        return result;
    }
  
   
}
