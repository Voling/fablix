Android app is contained in fablixmobile/
https://youtu.be/MgGjQpmqYv8
# Contributions:
# Dean Du: 
    fulltext search, autocomplete, android search, android login, android pagination, android movie page, android fulltext (implemented in backend), fuzzy search <br>
# Dylan Vo: 
    project: basic autocomplete, android basis, single android movie page, layouts <br>
# how we implemented fuzzy search:
we did a disjunction of "where MATCH(title) AGAINST( "+word1* +word2*"in boolean mode )" full text search boolean mode "or title like "%word1 word2%" or edth(title,"word1 word2",2) LIMIT 10;";
("where MATCH(title) AGAINST(? in boolean mode ) or title like ? or edth(title,?,2) LIMIT 10;")
<br>
so that full text search results will show up first if any full text result is existent and if not we can still see the fuzzy search result <br>

