### بخش اول 

# Polymorphism
برای انجام این بازآرایی به فایل codeGenerator.java نگاه می کنیم و مشاهده می کنیم که این فایل از switch case های طولانی تشکیل شده است. برای رفع این مشکل از polymorphism استفاده میکنیم. یک SemanticAction interface تعریف میکنیم که قرار است برای action ها استفاده شود:
![img.png](screenshots/img.png)
این interface یک تابع execute را تعریف میکند که کلاس های بچه باید آن را پیاده سازی کنند. همانطور که در تصویر مشاهده میشود توابع مختلفی که در تابع semanticFunction صدا زده میشدند اکنون باید داخل یک کلاس پیاده شوند که از SemanticAction interface پیروی میکند. با این تغییرات میتوانیم تابع semanticFunction را به صورت زیر تغییر دهیم:
![img.png](screenshots/img2.png)
مشاهده میکنیم که سایز کد به نسبت خیلی کمتر شده است. همچنین این پیاده سازی میتواند برای اضافه کردن قوانین جدید به semanticFunction بسیار مفید باشد و سرعت توسعه را افزایش دهد.

# Facade (1)
مشاهده می کنیم که CodeGenerator.java امکانات زیادی فراهم می کند که برای یک کامپایلر کامل مناسب است. اما می توانیم یک Facade اضافه کنیم که امکانات ساده تر و انتزاعی تری به ما می دهد که کد آن را در مثال زیر می بینیم:
![img.png](screenshots/img3.png)
در این کد ما تنها دستورات add, sub, jump را در اختیار استفاده کننده قرار می دهیم. این دستورات در تئوری برای پیاده سازی زیرمجموعه بزرگی از دستورات CodeGenerator اصلی کافی هستند (شاید حتی کل آن را نیز در نظر بگیرند! این را اثبات نکردیم.) این یک مثال از فراهم کردن تنها ویژگی های خاصی از دستورات کد اصلی است. 

# Facade (2)
در اینجا طبق کد زیر یک facade از SymbolTable فراهم می کنیم که اندکی انتزاعی تر از کد اصلی است:
![img.png](screenshots/img4.png)
![img_1.png](screenshots/img5.png)
این کد ویژگی های سظخ پایین کد را پنهان می کند (و لذا می توان پیاده سازی سطح پایین را با مشکلات کمتری تغییر داد.) دسترسی به klassها و یا دستوراتی مثل setLastType از دست کاربر خارج شده و دستوراتی مثل createClass جای آن را گرفته است. مشابه قسمت قبل این کد نیز یک انتزاع روی کد است که استفاده از آن را ساده تر می کند. 

# Seperate Query From Modifier
مشاهده می کنیم که در کد اصلی صدا زدن تابع printError از ErrorHandler باعث عوض شدن استیت hasError می شود که چون این متغیر بسته به برداشت و نحوه پیاده سازی شخص دیگر می تواند سبب ایجاد side effect های ناخواسته شود. برای این که این اتفاق نیفتد طبق بازآرایی دو منطق را از هم جدا می کنیم:
$$ screenshot from ErrorHandler.java here $$
متناسب با تغییرات در ErrorHandler تغییراتی را در بخش های دیگر اعمال می کنیم تا کد به درستی کار کند.