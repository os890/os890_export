as long as this module is located at the branch this module is a case-study.
currently there are no official examples because dev is ongoing.
news about this module will be posted at http://os890.blogspot.com/

this validation module provides extensible and typesafe constraints based on constraint aspects.
such constraints allow to implement custom values once and reuse them within the code-base.
changes can be applied at a single point.

an example: @Zip(Austria.class, US.class, MyCountry.class)
as you see you can provide your own values without changing the constraints or the validator.

your ideas are welcome as well!

planned constraints:
 - Choice
 - DateRange
 - EAN
 - EMail
 - EndsWith
 - Future
 - ISBN
 - PasswordStrength
 - Past
 - Payment
 - Protocol
 - Range
 - StartsWith
 - Substring
 - Unique
 - ValidatorBinding
 - Zip