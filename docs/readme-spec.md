# READMEdc File Specification
Version 2019-12-09

---

1. Naming Convention 
    - Scope
        + Public: __READMEdc.txt__
        + Private: __READMEdc.private.txt__
    - Sorting 
        __ prefixing can be used to bring README files to a front file list position
1. Content    
    - The content is represented as key:value pairs on one line:
        ```properties
        key:value
        ```
    - A key starts at the beginning of a line
    - A key is composed of any character:
        ```properties
        key1:value
        ```
    - The usage of spaces in key terms is __not recommended__.
    - Any leading and trailing whitespace is removed in keys 
    - All keys are optional
    - Duplicate keys are allowed and do not overwrite previous values
        ```properties
        key:value1
        key:value2
        ```
    - Keys with with empty values are ignored
    - All key names with preceding _ are reserved for system use 
    - A colon separates key and value
    - A value is considered to be terminated by any one of a line feed ('\n'), a carriage return ('\r'), or a carriage return followed immediately by a linefeed.
    - Multiline values can be indented with spaces:
        ```properties 
        key:Line1
         Line2
         Line3
        ```
    - All value information is treated as text
    - Multiple values for one key can be separated with a semicolon:
        ```properties
        key: value1; value2
        ```      
    - Lines starting with a hash sign (#) are identified as a comment. All the text from the hash sign to the end of the line is ignored.
        ```properties
        # This is a comment         
        ```

1. Example READMEdc.txt using a basic [Dublin Core](https://www.dublincore.org/) metadata schema:
    ```properties
    title:Getting started on BayCEER CLOUD
    creator:Oliver Archner; Stefan Holzheu
    subject:ownCloud
    subject:BayCEER
    description:A small guide to work with BayCEER CLOUD
      Installation 
      Getting started
      Demo 
    publisher:University of Bayreuth
    contributor:BayCEER IT Group
    date:2019-01-10
    type:Text
    format:Markdown
    identifier:
    source:
    language:en
    relation:
    coverage:
    rights:Creative commons share alike
    ```
