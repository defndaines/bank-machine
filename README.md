# bank-machine

Bank Machine is a clojure app that does the following:
 - reads a resource file containing drawings of bank account numbers,
 - translates those drawings into account numbers using digits,
 - analyzes the validity of those translations, and
 - displays the results to the user.

It is based on the Bank OCR coding kata here:
http://codingdojo.org/kata/BankOCR/#problem-description 

## Installation

This app assumes that java is already installed on your machine.
Download the standalone jar file from the source code.
It is located here: target/uberjar/bank-machine-0.1.0-standalone.jar.

## Usage

This project must be run from the command line. It takes no arguments. 

    $ java -jar bank-machine-0.1.0-standalone.jar


## Future Enhancements:

 - Implementing a comprehensive test battery
 - Allowing users to supply thier own bank machine files for processing
 

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
