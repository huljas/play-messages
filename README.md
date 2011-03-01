Play Messages Module
====================

Provides a tool for localizing your application in <host>:<port>/@messages.

Tested with play 1.1 on windows and linux.

## Features

- Scans through your application sources and tries to locate all localization keys
- The keys are divided into new, existing and obsolete
- You can edit values for all of them
- You cam select the language to localize
- You can select another language to compare with
- Ignore list for keys that should not be shown as new keys. Mainly intended for keys that are falsely identified as localization keys. These are usually parts of generated keys.
- Keep list for keys that are should be treated as normal keys although they are not found in the sources. These are usually keys that are generated.
- Possibility to remove existing and add new keys

## Usage

Add the module to your application `application.conf`

    module.messages=[path to module]

Add the module to the `routes`

    *     /                       module:play-messages

Now you can localize your application using the @messages for example

    http://localhost:9000/@messages



