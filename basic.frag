void main() {
  float g = gl_Color.y + 0.5f;
  float r = gl_Color.x - 0.5f;  
  gl_FragColor = vec4(r, g, 0.0f, 1.0f);
}
