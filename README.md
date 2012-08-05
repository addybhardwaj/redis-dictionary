redis-dictionary
================

Implements basic word lookup mechanism using redis as the indexing engine.  This implementation has been inspired by the following article http://antirez.com/post/autocomplete-with-redis.html

Summary
-------
This library provides a mechanism to index and lookup data based on keywords extracted from the data. At the heart of this library are word dictionaries which are used to index the data based on keywords extracted from the data. These dictionaries can then be used to lookup data based on related keywords. Currently implemented word dictionaries are:
* Word completion dictionary : based on partial or full keywords it provides a list of keywords (complete) stored against indexed data. For instance if keyword **kitty** was stored then it can be lookup using partial word i.e. **ki**, **kit**, etc. (starts with partern is used)                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
* Metaphone dictionary (sounds like) : indexes words by their Metaphone value. This allows mistyped words to be looked up easily i.e. if **cat** was indexed, it can be lookup by **kat**, **kit**, etc. This uses apache codec library which provides Metaphone calculation.

On top of the word dictionaries, the library provides a lightweight search service implementation `RedisKeywordDataRepository` which implements interface `SearchableKeywordDataRepository`. This implementation handles the splitting of words from the phrase provided for indexing. It is able to filter out join characters like `.` `(` etc. 

Usage
--------
In this example we would want to index a list of category objects represented Category domain object that consists of id, name and description. 

```
public class Category {
    private String id;
    private String name;
    private String description;
    
    ... getter and setters ...
}
```

1. Firstly, implement a `KeywordAndIdExtractor` which will be required for indexing data. 
```
public CategoryKeywordAndIdExtractor implements KeywordAndIdExtractor<Category> {

    @Override
    public String extractKeywords(Category data) {
        StringBuilder sb = new StringBuilder(data.getName());
        sb.append(" ").append(data.getDescription());
        
        return sb.toString();
    }

    @Override
    public String extractId(Category data) {
        return data.getId();
    }         
    ...
```
2. Configure Redis instance and load the spring bean configuration : Unit test `RedisKeywordDataRepositoryTest` provides spring way of setting the properties. Example properties are provided at `test/resources/test-repository.properties`. **Note**: these properties are not provided by PropertyPlaceholderConfigurer but by properties bean named `repo`
3. Now start indexing
```
    @Inject
    private RedisKeywordDataRepository redisKeywordDataRepository;

    ....
    
    public void storeCategory(Category category) {
        //store category object in data store ...
        //now index the category
        redisKeywordDataRepository.indexDataByKeywords("categoryIndex", new CategoryKeywordAndIdExtractor(), category);
    }
```
4. Search the indexed categories
```    
    public List<String> searchCategories(String phrase) {
        //returns category ids
        return redisKeywordDataRepository.indexDataByKeywords("categoryIndex", phrase);
    }
```


How to use this library in maven
------------------------

```
<dependency>
    <groupId>com.intelladept.oss.redis</groupId>
    <artifactId>redis-dictionary</artifactId>
    <!-- Note: use the most recent version available -->
    <version>0.1.0-SNAPSHOT</version>
</dependency>

maven repository which holds the artifacts
https://repository-addy.forge.cloudbees.com/snapshot/
```