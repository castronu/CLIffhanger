CLIffhanger create a web interface for a batch program, for free.

Installation (linux/ MacOSX):

```
curl www.diegocastronuovo.com/cliffhanger/installCliffhanger.sh | sh
```

How does it works? Simply provide an xml configuration for your command-line-based program and start CLIffhanger, go to http://localhost:3000 and enjoy your free web interface. 

This is a configuration example for the grep program:

```
<executable>
    <path>grep</path>
    <description>This program search a given pattern in a given input file.</description>
    <options>
        <option>
            <name>-n</name>
            <required>false</required>
            <description>Each output line is preceded by its relative line number in the
                file, starting at line 1.  The line number counter is reset for
                each file processed.  This option is ignored if -c, -L, -l, or -q
                is specified.</description>
        </option>
        <option>
            <name>-r</name>
            <required>false</required>
            <description>Recursively search subdirectories listed.</description>
        </option>
        <option>
            <name>-h</name>
            <required>false</required>
            <description>Never print filename headers (i.e. filenames) with output lines.</description>
        </option>
        <option>
            <name>-i</name>
            <required>false</required>
            <description>--ignore-case
                Perform case insensitive matching.  By default, grep is case sen-
                sitive.</description>
        </option>
    </options>
    <arguments>
        <argument>
            <name>pattern</name>
        </argument>
        <argument>
            <name>inputFile</name>
        </argument>
    </arguments>
    <logs>
        <log>/tmp/output.log</log>
    </logs>
</executable>
```

Install CLIffhanger, save this content in a file, e.g. configGrep.xml, then run:

```
cliffhanger configGrep.xml
```

Open your browser at http://localhost:3000:

![alt tag](http://www.diegocastronuovo.com/cliffhanger/cliffhanger.png)
