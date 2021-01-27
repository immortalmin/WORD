<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$json_string = file_get_contents('php://input');
$body = json_decode($json_string,true);
$uid = $body['uid'];
$result =mysqli_query($conn,"SELECT words.`wid`,words.`word_group`,words.`C_meaning`,user.`username` 'source',recite.`collect`,recite.`correct_times`,recite.`error_times`,recite.`prof_flag`,recite.`last_date` FROM words,recite,user WHERE user.`uid`=words.`source` AND words.`wid`=recite.`wid` and recite.uid=".$uid);

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

