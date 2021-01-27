<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$json_string = file_get_contents('php://input');
$body = json_decode($json_string,true);
$uid = $body['uid'];
$wid = $body['wid'];
$dict_source = $body['dict_source'];
if($dict_source=="0"){
	$result = mysqli_query($conn,'SELECT cid,gid,words.`wid`,word_group AS "word_en",C_meaning AS "word_ch",correct_times,error_times,last_date,review_date,user.`username` AS "source","0" AS "dict_source" FROM user,words LEFT JOIN collect ON words.`wid`=collect.`wid` AND collect.`uid`='.$uid.' WHERE words.`source`=user.`uid` AND words.`wid`='.$wid);
}else if($dict_source=="1"){
	$result = mysqli_query($conn,'SELECT cid,gid,k_words.`wid`,word_en,word_ch,correct_times,error_times,last_date,review_date,"柯林斯" AS "source","1" AS "dict_source" FROM k_words LEFT JOIN collect ON k_words.`wid`=collect.`kid` AND collect.`uid`='.$uid.' WHERE k_words.`wid`='.$wid);
}
//$result =mysqli_query($conn,"SELECT words.`wid`,words.`word_group`,words.`C_meaning`,user.`username` 'source',recite.`collect`,recite.`correct_times`,recite.`error_times`,recite.`prof_flag`,recite.`last_date` FROM words,recite,user WHERE words.`source`=user.`uid` AND words.`wid`=recite.`wid` and words.`wid`=".$wid." and recite.uid=".$uid);

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

