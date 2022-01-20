# OC Elastic Search Provider

_A microservice to index and search metadata information in ownCloud_

+ [Installation Instruction](docs/readme-install.md)
+ [Query Syntax](docs/query_syntax.md)
+ [ReadmeDC File Specification](docs/readme-spec.md)
+ [Search UI Prototype](https://github.com/BayCEER/oc-search-ui)
+ [REST API Documentation](https://bayceer.github.io/oc-search-elastic/rest-api.html)

## History
### Version 1.1.13, Jan, 2022
- Fix: Remove [BOM](https://de.wikipedia.org/wiki/Byte_Order_Mark) in UTF content string 

### Version 1.1.12, Dec, 2021
- Fix: log4j vulnerability

### Version 1.1.11, May, 2021
- New: Search in path
- New: Sorted field list
- New: Sort search results by path and score

### Version 1.1.10, March, 2020
- Fixed null values in terms query

### Version 1.1.9, March, 2020
- Fixed null values in search query

### Version 1.1.8, January, 2020
- Fixed multi value bug

### Version 1.1.7, December, 2019
- Client side field validation before indexing
 
### Version 1.1.0, December, 2019
- Term search
- ReadmeDC list support

### Version 1.0.0, December, 2019
- Migration from Apache Lucene to Elastic Search
- Field Aggregation
- Field Filter  

