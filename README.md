####Forgetful Map

The forgetful map is designed to store a key, and some content up to a capacity limit passed by the user as an `Integer` on initialization.

eg, `ForgetfulMap forgetfulMap = new ForgetfulMap(200)`

where `200` is the maximum number of entries allowed.

Upon reaching the capacity limit, the entry retrieved by the user the least is first removed, then the new entry is added.

If there are entries with equal access counts, the entry with the oldest access date is removed.

The methods available on the forgetfulMap are as follows: 

`void add (K key, V value);`
`V find (K key);`
`void update(K key, V value);`
`void delete (K key);`

If the key does not exist when using the update method, the key will be created.
The update method does not increase the access count or last accessed time.


The Forgetful Map can be consumed by adding the following to your project: 

`<groupId>com.origami</groupId>
 <artifactId>ForgetfulMap</artifactId>
 <version>1.0.0</version>`


--------------

Design Considerations

As per the specifications, the map had to take a key and content of unspecified types, therefore I have used generics in the implementation.

The specification required a 'suitable tie-breaker' when the retrieval counts for some entries were the same. Therefore, I decided to preserve the entry used most recently.

Possible improvements to be made: 
- investigate the performance hit of `synchronized`, and determine if there is a better approach to ensure thread safety. 

