<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$json_string = file_get_contents('php://input');
$body = json_decode($json_string,true);
//$wid = $body['wid'];
//$uid = $body['uid'];
$collect = $body["collect"];
//$dict_source = $body['dict_source'];
if(strcmp($collect,"0")==0){//取消收藏
	$cid = $body['cid'];
	mysqli_query($conn,'DELETE FROM collect WHERE cid='.$cid);
}else{//添加收藏
	$uid = $body['uid'];
	$wid = $body['wid'];
	$dict_source = $body['dict_source'];
	if($dict_source==0){
		mysqli_query($conn,'INSERT INTO collect(uid,gid,wid) VALUES ('.$uid.',0,'.$wid.')');
		$result = mysqli_query($conn,'SELECT cid,collect.`uid`,gid,words.`wid`,word_group AS "word_en",C_meaning AS "word_ch",correct_times,error_times,last_date,user.`username` AS "source" FROM user,words LEFT JOIN collect ON words.`wid`=collect.`wid` AND collect.`uid`='.$uid.' WHERE words.`source`=user.`uid` AND words.`wid`='.$wid);
		//$result = mysqli_query($conn,'SELECT cid,uid,gid,words.`wid`,word_group AS "word_en",C_meaning AS "word_ch",correct_times,error_times,last_date FROM words LEFT JOIN collect ON words.`wid`=collect.`wid` AND collect.`uid`='.$uid.' WHERE words.`wid`='.$wid);
	}else{
		mysqli_query($conn,'INSERT INTO collect(uid,gid,kid) VALUES ('.$uid.',0,'.$wid.');');
		$result = mysqli_query($conn,'SELECT cid,uid,gid,k_words.`wid`,word_en,word_ch,correct_times,error_times,last_date,"柯林斯" AS "source" FROM k_words LEFT JOIN collect ON k_words.`wid`=collect.`kid` AND collect.`uid`='.$uid.' WHERE k_words.`wid`='.$wid);
		//$result = mysqli_query($conn,'SELECT cid,uid,gid,k_words.`wid`,word_en,word_ch,correct_times,error_times,last_date FROM k_words LEFT JOIN collect ON k_words.`wid`=collect.`kid` AND collect.`uid`='.$uid.' WHERE k_words.`wid`='.$wid);
	}
}

//if($dict_source=="0"){
//	$result = mysqli_query($conn,'SELECT cid,collect.`uid`,gid,words.`wid`,word_group AS "word_en",C_meaning AS "word_ch",correct_times,error_times,last_date,user.`username` AS "source" FROM user,words LEFT JOIN collect ON words.`wid`=collect.`wid` AND collect.`uid`='.$uid.' WHERE words.`source`=user.`uid` AND words.`wid`='.$wid);
//}else if($dict_source=="1"){
//	$result = mysqli_query($conn,'SELECT cid,uid,gid,k_words.`wid`,word_en,word_ch,correct_times,error_times,last_date,"柯林斯" AS "source" FROM k_words LEFT JOIN collect ON k_words.`wid`=collect.`kid` AND collect.`uid`='.$uid.' WHERE k_words.`wid`='.$wid);
//}

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

