data = LOAD '$INPUT/data.csv' using PigStorage(',') as (ID,Day,Hour,User,Text);
data_words = foreach data generate Day, Hour, FLATTEN(TOKENIZE(REPLACE(LOWER(TRIM(Text)),'[\\p{Punct},\\p{Cntrl}]',''))) as word;
g = group data_words by (Day, Hour, word);
g_flatten = foreach g {
    generate flatten(group), COUNT(data_words) as Count;
};

positive_words = load '$INPUT/positive-words.txt' as (word);
positive_join = JOIN g_flatten by group::word, positive_words by word;
positive_join_projection = foreach positive_join generate g_flatten::group::Day as Day, g_flatten::group::Hour as Hour, g_flatten::group::word as Word, g_flatten::Count as Count;

positive_group = group positive_join_projection by (Day, Hour);
positive_sort = foreach positive_group {
    generate group, SUM(positive_join_projection.$3) as Sum;
}

negative_words = load '$INPUT/negative-words.txt' as (word);
negative_join = JOIN g_flatten by group::word, negative_words by word;
negative_join_projection = foreach negative_join generate g_flatten::group::Day as Day, g_flatten::group::Hour as Hour, g_flatten::group::word as Word, g_flatten::Count as Count;

negative_group = group negative_join_projection by (Day, Hour);
negative_sort = foreach negative_group {
    generate group, SUM(negative_join_projection.$3) as Sum;
}


stop_words = load '$INPUT/stopwords.txt' as (word);
diff = COGROUP g_flatten BY group::word, stop_words BY word;
filt = FILTER diff by IsEmpty(stop_words);
t = foreach filt generate flatten(g_flatten) as (Day, Hour, Word, Count);


stop_group = group t by (Day, Hour);
stop_sort = foreach stop_group {
    sorted = ORDER t by Count DESC;
    sorted_top = LIMIT sorted 30;
    generate group, sorted_top.($2,$3) as WordCounts;
}


total_result = JOIN positive_sort by group, negative_sort by group, stop_sort by group;
out_result = foreach total_result generate positive_sort::group as Time, positive_sort::Sum as PositiveSum, negative_sort::Sum as NegativeSum, stop_sort::WordCounts as WordCounts;

store out_result into '$OUTPUT' USING JsonStorage();
