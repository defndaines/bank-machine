# bank-machine

Bank Machine is a clojure app that does the following:
 - reads a resource file containing drawings of bank account numbers,
 - translates those drawings into account numbers using digits,
 - analyzes the validity of those translations, and
 - displays the results to the user.

It is based on the Bank OCR coding kata here:
http://codingdojo.org/kata/BankOCR/#problem-description 

## Installation

This app assumes that java and leiningen are already installed on your machine.
Simply clone the repo using git or download the project in a zip file from GitHub.

## Usage

This project must be run from the command line. It takes no arguments. 

    $ lein run
    
If you wish to run the project using your own resource file containing machine drawings
of account numbers, please copy/paste the text of your file into the project file,
/resources/files/machine_file.txt. (Please keep the file name the same.)

Then run the following to ensure proper formatting has been maintained in this file:

    $ lein test
    
The proper formatting is as follows:
 - each line is 27 chars long
 - each line contains only spaces, underscores, and pipe characters; except every 4th line
 - each 4th line is blank
 - an empty line must be the last line in the file (following a preceding empty 4th line)
 
Make alterations to this file until all tests pass. Then you can run the project using the same
run command mentioned above.

## Possible Future Enhancements:

 - Implement batch processing of machine files, which would allow users to add their own bank
 machine files in the resource folder for processing as many files as they like. 
 

## License

Copyright © 2019 Rachelle Pinckney

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
