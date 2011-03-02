Play Messages Module
====================

This module gives you a tool in @messages for localizing your application.

Tested with play 1.1 on windows and linux.

## Features
- Web based tool for localizing your application
- Finds localization keys from your sources
- Keys are clearly divided into new, existing and obsolete
- Allows you to edit localizations with one language while comparing it with another
- Ignore list for keys that should not be shown as new keys, intended for keys that are falsely identified as localization keys
- Keep list for keys that are should be treated as normal keys although they are not found in the sources
- Add new keys if needed
- Remove existing keys
- Localizations are saved in alphabetical order in your application's messages files

## Usage

Add the module to your application `application.conf`

    module.messages=[path to module]

Add the module to the `routes`

    *     /     module:play-messages

Start your application and access the tool in @messages:

    http://localhost:9000/@messages



