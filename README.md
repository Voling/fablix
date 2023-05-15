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
        we used in memory hashing to help check in-consistency and store things like starId so that we can have quick access 
        to them later.
    # 2. batch inssertion
        we did 500 insertion per batch
# files that use prepared statement:
all endpoint files use all prepared statements, even for our xml parsing files(which is unnecessary I know but we are just used to it)

# the log files for xml are in xmlparsing directory 
