<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$json_string = file_get_contents('php://input');
$body = json_decode($json_string,true);
$mount = $body['mount'];
$uid = $body['uid'];
$result =mysqli_query($conn,'SELECT * FROM (SELECT cid,gid,collect.`wid`,word_group AS word_en,C_meaning AS word_ch,correct_times,error_times,last_date,review_date,"0" AS dict_source FROM collect INNER JOIN words ON collect.`wid`=words.`wid` WHERE uid='.$uid.' AND correct_times<5 UNION ALL SELECT cid,gid,collect.`kid` AS wid,word_en,word_ch,correct_times,error_times,last_date,review_date,"1" AS dict_source FROM collect INNER JOIN k_words ON collect.`kid`=k_words.`wid` WHERE uid='.$uid.' AND correct_times<5) con ORDER BY correct_times,error_times DESC LIMIT '.$mount);

while ($shopInfo = mysqli_fetch_array($result,MYSQLI_ASSOC)){ //返回查询结果到数组
	$output[]=$shopInfo;
}
echo json_encode( $output,JSON_UNESCAPED_UNICODE);
//4.释放内存中的查询结果
mysqli_free_result($result);
//5.关闭连接
mysqli_close($conn);
?>
