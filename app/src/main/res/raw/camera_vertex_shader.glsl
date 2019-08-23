uniform mat4 uMVPMatrix;//标准化设备坐标点（NDC）MVP变换矩阵
uniform mat4 uTexMatrix;//纹理坐标变换矩阵
attribute vec4 aPosition;//NDK坐标点
attribute vec4 aTextureCoord;//纹理坐标点
varying vec2 vTextureCoord;//纹理坐标点变换后输出

void main() {
    gl_Position = uMVPMatrix * aPosition;//对NDC坐标点进行变换，赋值给gl_Position
    vTextureCoord = (uTexMatrix * aTextureCoord).xy;//队纹理坐标点进行变换，赋值后作为片段着色器输出
}
