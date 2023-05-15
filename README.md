# s23-122b-tryna_find_a_teammate
https://www.youtube.com/watch?v=GXzv1FKrCiU
Log files are located at /xmlparsing/

# Contributions:
# Dean Du: 
    project: stored procedure, encrypted, xml import, preparedstatement, dashboard <br>
# Dylan Vo: 
    project: dashboard, html, https, xml import, recaptcha <br>

# our substring matching rules:
# searching:
    title: %title% (if contains that anywhere in the string)
    director: %director% (if contains that anywhere in the string)
    year: exact match
    star: %star% (if contains that anywhere in the string)
    browsing:
    genre:exact match
    title-number:%title-number% (if contains that anywhere in the string)
    title-char: title-char% (if pattern matches start of the string)
# two optimization techniques:
    # 1. in memory hashing:
        we used in memory hash table to help check in-consistency and store things like starId so that we can have quick access 
        to them later, without this we would have to query the database every time we check inconsistency or fetch starId, it would be 
        tremendous overhead for time
        # 2. batch inssertion
        we did 500 insertion per batch
        without batching we are doing a lot more transations, which is again a lot more overhead for time
# files that use prepared statement:
all endpoint files use all prepared statements, even for our xml parsing files(which is unnecessary I know but we are just used to it)

# the log files for xml are in xmlparsing directory 
