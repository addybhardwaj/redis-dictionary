redis-dictionary
================

Provides a mechanism to build word dictionary based on data sets and ability to search the data set by words and partial words appearing in the set.

For instance, if the data set include category names (string), it would be broken down into individual words and indexed.
After categories are indexed, they can be searched by by words in the category name or partial words in the categories.
Example:
Category: Baby and toddler
Words indexed: (baby), (and), (toddler)
Example search keywords: (b), (ba), (baby), (a), etc

How the category names are broken down is customisable by using KeywordAndIdExtractor. This can be used to filter out
join words like and, a, if, etc

How to use this library in maven
------------------------

<dependency>
    <groupId>com.intelladept.oss.redis</groupId>
    <artifactId>redis-dictionary</artifactId>
    <!-- Note: use the most recent version available -->
    <version>1.0.0-SNAPSHOT</version>
</dependency>

maven repository which holds the artifacts
https://repository-addy.forge.cloudbees.com/snapshot/

