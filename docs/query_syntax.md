# READMEdc Query Syntax
Version 2019-12-02

---

READMEdc queries can be defined according to the [Apache Lucene Simple Query Parser](http://lucene.apache.org/core/8_2_0/queryparser/org/apache/lucene/queryparser/simple/SimpleQueryParser.html) syntax.

__Syntax__ 
`+` AND operation: `token1+token2`
`|` OR operation: `token1|token2`
`-` Negates a single token: `-token0` 
`"` Creates a phrase: `"term1 term2 ..."` 
`*` at the end of terms specifies prefix query: `term*`
`~N` at the end of terms specifies fuzzy query: `term~1` 
`~N`  at the end of phrases specifies near query: `"term1 term2"~5` 
`(` and `)` specifies precedence: `token1 + (token2 | token3) `

Whitespace, '\n', '\r' or '\t' may be used to delimit tokens. 
The default operator is OR if no other operator is specified. For example, the following will OR token1 and token2 together: `token1 token2`.  
Normal operator precedence will be simple order from right to left. 

__Escaping__ 
Escaping can be done with the escape character: `\`.
The following characters must be escaped: `+ | " ( ) ' \` 

Special cases:
+ Not operator: The __first__ occurrence in a term must be escaped:
`-term1` -- Specifies NOT operation against term1 
`\-term1` -- Searches for the term -term1. 
+ Prefix query: The __last__ character in a term must be escaped:
`term1*` -- Searches for the prefix term1 
`term1\*` -- Searches for the term term1* 

