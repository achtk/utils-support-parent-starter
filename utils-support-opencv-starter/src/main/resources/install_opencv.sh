echo "yum源存储在/home/zxb2/env/ 目录下"
mkdir -p /home/zxb2/env/opencv_make
cd /home/zxb2/env/opencv_make
mkdir release
echo "下载OpenCV源码"
wget https://github.com/opencv/opencv/archive/3.4.3.tar.gz
tar -zvxf opencv-3.4.3.tar.gz
cd opencv-3.4.3
mkdir build
cd build
echo "进行CMake"
yum -y install cmake gcc gcc-c++ gtk+-devel gimp-devel gimp-devel-tools gimp-help-browser zlib-devel libtiff-devel libjpeg-devel libpng-devel gstreamer-devel libavc1394-devel libraw1394-devel libdc1394-devel jasper-devel jasper-utils swig python libtool nasm build-essential ant
cmake -D CMAKE_BUILD_TYPE=RELEASE -D CMAKE_INSTALL_PREFIX=/home/zxb2/env/opencv_make/release -DBUILD_TESTS=OFF ..
make -j8
make
cd /home/zxb2/env/opencv_make/opencv-3.4.3/build/lib
echo "下面是你想要的文件了"
ls
