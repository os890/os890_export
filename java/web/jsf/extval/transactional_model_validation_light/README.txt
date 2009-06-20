Requirements:
 - ExtVal 1.x.3-SNAPSHOT+

This add-on is a prototype for lightweight transactional model validation.
It also offers basic support for class-level validation. The original model values (before the model updated) are restored, if there is a violation during model aware and/or class level validation.

In comparison to the full transactional model validation prototype, this add-on doesn't require cglib or spring.
Attention: other changes e.g. executed by value-change-listeners,... don't get reverted - you have to consider it on your own.
