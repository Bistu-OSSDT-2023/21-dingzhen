# 使用说明

车牌提取系统可以在各种主流操作系统上运行，包括Windows、Linux和macOS。以下是系统的运行环境要求：OpenCV Java

从GitHub仓库中克隆或下载源代码。 准备包含车辆的图像文件。 运行系统的主程序，选择w图像文件路径。 系统将自动进行车牌检测、字符分割和字符识别，并输出识别结果。


编译方法：

首先确保安装了vcpkg

在终端中输入

.\vcpkg\vcpkg install tesseract:x64-windows

.\vcpkg\vcpkg install opencv4:x64-windows

.\vcpkg\vcpkg integrate install

git clone https://github.com/Bistu-OSSDT-2023/28-dingzhen.git


下载git clone  https://github.com/Bistu-OSSDT-2023/28-dingzhen.git

打开release/phtotuploader

用eclipse项目导入 运行imageuploader

随后选择图片即可