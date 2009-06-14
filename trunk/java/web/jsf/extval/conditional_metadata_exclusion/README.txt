Requirements:
 - ExtVal 1.x.2

Allows to provide fine-grained exclude conditions for metadata.
Attention: due to implicit processing order it isn't compatible with some other metadata add-ons of os890.
Solution:
Use the ReorderInterceptorsStartupListener manually in your application or use the advanced-metadata add-on.