Change Log
==========

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
