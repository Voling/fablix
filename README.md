# s23-122b-tryna_find_a_teammate
https://www.youtube.com/watch?v=GXzv1FKrCiU
Log files are located at /xmlparsing/

Contributions:
Dean Du: 
    project: stored procedure, encrypted, xml import, preparedstatement
Dylan Vo: 
    project: dashboard, html, https, xml import, recaptcha

our substring matching rules:
searching:
    title: %title% (if contains that anywhere in the string)
    director: %director% (if contains that anywhere in the string)
    year: exact match
    star: %star% (if contains that anywhere in the string)
    browsing:
    genre:exact match
    title-number:%title-number% (if contains that anywhere in the string)
    title-char: title-char% (if pattern matches start of the string)
