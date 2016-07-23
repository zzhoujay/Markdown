# MarkDown

> Android平台的原生Markdown解析器

Markdown文本直接转换成Spanned，直接设置给TextView即可完成显示

遵循 Github Flavored Markdown 标准 _（如果大家发现有和GFM标准不相符合的地方，欢迎指出）_

### 效果展示

![效果图](image/img1.jpg)

### 使用

```
Markdown.fromMarkdown(text,imageGetter,textView);
```

**注意：** 此方法需要在textView的Measure完成后调用，因为需要获取textView的宽高

例子：
```
textView.post(new Runnable() {
     @Override
     public void run() {
     Spanned spanned = MarkDown.fromMarkdown(stream, new Html.ImageGetter() {
           @Override
           public Drawable getDrawable(String source) {
                 Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
                 drawable.setBounds(0, 0, 400, 400);
                 return drawable;
           }
     }, textView);
     textView.setText(spanned);
}
```

### Use in Gradle

`compile 'com.zzhoujay.markdown:markdown:0.0.2'`

**注意：** 当前并非稳定版，仅供尝鲜使用

### 项目进度

* 已完成大部分功能开发
* 一些细节优化和接口待开发

### 已知问题

* 引用块内不支持Setext-style的标题（后续会想办法修复）
* 不支持表格


### 后续计划

* 修复完善当前版本
* 整合进 [RichText](https://github.com/zzhoujay/RichText)
* 修复一些已知问题

_by zzhoujay_
