Requirements:
 - ExtVal 1.x.3-SNAPSHOT+

Allows to provide e.g. ValidationParameters for 3rd party metadata e.g.:
@VirtualMetaData(target = javax.persistence.Column.class, parameters = ViolationSeverity.Warn.class)
