Play Messages Module
====================

This module gives you a tool for localizing your application.

Tested with play 2.1.1 on windows and linux.

## Features
- Web based tool for localizing your application
- Finds localization keys from your sources
- Keys are clearly divided into new, existing and obsolete
- Allows you to edit all localizations within one table
- Ignore list for keys that should not be shown as new keys, intended for keys that are falsely identified as localization keys
- Keep list for keys that are should be treated as normal keys although they are not found in the sources
- Remove existing keys
- Localizations are saved in alphabetical order in your application's messages file

## Usage

Add the module dependency to your application `Build.scala`

    "de.corux" %% "play-messages" % "[2.0,)"

Add the module to the `routes`

    -> /@messages    play.messages.Routes

Start your application and access the tool in @messages:

    http://localhost:9000/@messages

You can specify the source folders the tool scans in the `application.conf`, for example

    messages.srcDir=app,public/javascripts

if you want to include localizations in your javascripts. Default value for this property is `app`.
Take a look at [reference.conf](conf/reference.conf) for all available options.
