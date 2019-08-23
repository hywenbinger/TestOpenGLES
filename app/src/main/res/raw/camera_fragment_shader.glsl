#extension GL_OES_EGL_image_external : require //声明使用OES扩展纹理
precision mediump float;//精度声明
varying vec2 vTextureCoord;//顶点着色器输出经图元装配和栅格化后的纹理坐标点序列
uniform samplerExternalOES  sTexture;//OES纹理，接收相机纹理作为输入

void main() {
    gl_FragColor=texture2D(sTexture, vTextureCoord);
}

