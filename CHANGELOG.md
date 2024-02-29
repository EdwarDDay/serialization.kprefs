<!--
SPDX-FileCopyrightText: 2020-2024 Eduard Wolf

SPDX-License-Identifier: Apache-2.0
-->

Change Log
==========

Version 0.12.1 *(2023-01-24)*
----------------------------

Fix issue with reading encoded nullable collections

Version 0.12.0 *(2022-11-26)*
----------------------------

BREAKING CHANGE:

`null` isn't handled as default value anymore. If you relied on this behavior, please explicitly declare `null` as
default value.

Better support for `null` values

Support for default values at root level

Upgrade kotlin to 1.7.21

Upgrade kotlinx.serialization to 1.4.1

Version 0.11.0 *(2021-10-03)*
----------------------------

Support for synchronization on read and write

Upgrade kotlin to 1.5.31

Upgrade kotlinx.serialization to 1.3.0

Version 0.10.0 *(2021-05-24)*
----------------------------

Support default values in properties

Version 0.9.0 *(2021-05-07)*
----------------------------

Upgrade kotlin to 1.5.0

Upgrade kotlinx.serialization to 1.2.0

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
