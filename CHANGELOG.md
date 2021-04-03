Change Log
==========

Version 0.8.0 *(2021-04-03)*
----------------------------

Upgrade kotlin to 1.4.32
Upgrade kotlinx.serialization to 1.1.0

Version 0.7.1 *(2021-02-15)*
----------------------------

Move to maven central

Version 0.7.0 *(2020-11-23)*
----------------------------

Upgrade kotlin and kotlinx.serialization
Fix some docs

Version 0.6.0 *(2020-11-14)*
----------------------------

Fix some docs

Version 0.5.1 *(2020-11-13)*
----------------------------

Change artifact from `it.edwardday.serialization:kpref` to  `net.edwardday.serialization:kpref`

Version 0.5.0 *(2020-11-13)*
----------------------------

Change package from `it.edwardday.serialization.preferences` to  `net.edwardday.serialization.preferences`

Version 0.4.0 *(2020-11-07)*
----------------------------

Enum handling only via `name` serialization - removed Int decoding
Support native Set<String> serialization

BREAKING:
min SDK level raised to 11

Version 0.3.0 *(2020-11-03)*
----------------------------

Support delegated properties
Do not use SharedPreferences default value for primitives

Version 0.2.0 *(2020-10-26)*
----------------------------

Delete old preference values before writing new ones to not have an unwanted mix of old and new values.

Version 0.1.0 *(2020-10-25)*
----------------------------

Initial release
