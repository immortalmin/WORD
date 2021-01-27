<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$json_string = file_get_contents('php://input');
$body = json_decode($json_string,true);
$word = $body['word'];
//$uid = $body['uid'];
$newword = "%";
for($i=0;$i<strlen($word);$i++){
	$newword .= $word[$i];
	$newword .= "%";
}
$newword = str_replace("\"","\\\"",$newword);
//2021/01/05
//$result = mysqli_query($conn,'SELECT words.`wid`,word_group AS "word_en",C_meaning  AS "word_ch","0" AS "dict_source",cid,gid,correct_times,error_times,last_date FROM words left join collect ON collect.`wid`=words.`wid` WHERE words.`word_group` LIKE "'.$newword.'" UNION SELECT k_words.`wid`,word_en AS "word_en",word_ch AS "word_ch","1" AS "dict_source",cid,gid,correct_times,error_times,last_date FROM k_words left join collect ON k_words.`wid`=collect.`kid` WHERE word_en LIKE "'.$newword.'" ORDER BY LENGTH(word_en) LIMIT 20;');

$result = mysqli_query($conn,"SELECT words.`wid`,word_group AS \"word_en\",C_meaning  AS \"word_ch\",\"0\" AS \"dict_source\",cid,gid,correct_times,error_times,last_date,review_date FROM words left join collect ON collect.`wid`=words.`wid` WHERE words.`word_group` LIKE \"".$newword."\" UNION SELECT k_words.`wid`,word_en AS \"word_en\",word_ch AS \"word_ch\",\"1\" AS \"dict_source\",cid,gid,correct_times,error_times,last_date,review_date FROM k_words left join collect ON k_words.`wid`=collect.`kid` WHERE word_en LIKE \"".$newword."\" ORDER BY LENGTH(word_en) LIMIT 20;");

while ($shopInfo = mysqli_fetch_array($result,MYSQLI_ASSOC)){ //返回查询结果到数组
	$output[]=$shopInfo;
}
if($output){
	echo json_encode( $output,JSON_UNESCAPED_UNICODE);
}else{
	echo json_encode([],JSON_UNESCAPED_UNICODE);
}

//4.释放内存中的查询结果
mysqli_free_result($result);
//5.关闭连接
mysqli_close($conn);
?>

