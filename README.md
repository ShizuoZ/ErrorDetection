# Human-Coding-Checker
In modern hospital, some medical data can be collected automatically, some remains manual data coding. This app provides data-driven abnormal detection to discover human coding errors in workflow data. We integrated the algorithms into a light-weight Java application, Human Coding Checker, which possesses three main function to help user efficiently locate and interpret the coding errors. Users might have a overview in table panel, which helps revise highlighted error candidate and export to a new file. Chart section is designed to display distribution of data responsible to current activity, this might better interpret the reason why those data are highlighted as errors. A highlight scroll bar may provide a overview of error candidates and helps user quickly locating them by one click.

## Quick Start:

Download zip file or use: git clone.

Then, there are two ways to start: 

1. Find jar file in dist/HumanCodingChecker.jar

2. User can also run MyJXTable.java to start.

## Test:

The input format is displayed in: data/Sample_Data.csv

Currently, only csv file is accepted and each entry should be consisted of: id, activity, start time and end time.
