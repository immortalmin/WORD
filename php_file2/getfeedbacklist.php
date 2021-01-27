<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$json_string = file_get_contents('php://input');
$body = json_decode($json_string,true);
$AllOrPerson = $body['AllOrPerson'];//0:获取所有的用户反馈；1:获取指定用户的用户反馈
if($AllOrPerson==0){
	$result = mysqli_query($conn,'SELECT eid AS "fid",user.`uid`,username,profile_photo,phone_model,description,contact,add_time,progress,img_path,"0" as "what" FROM error_feedback INNER JOIN user where error_feedback.`uid`=user.`uid` UNION SELECT fid,user.`uid`,username,profile_photo,"null" AS "phone_model",description,contact,add_time,progress,img_path,"1" as "what" FROM feature_suggestions INNER JOIN user where feature_suggestions.`uid`=user.`uid` order by add_time desc;');
}else{
	$uid = $body['uid'];
	$result = mysqli_query($conn,'SELECT eid AS "fid",user.`uid`,username,profile_photo,phone_model,description,contact,add_time,progress,img_path,"0" as "what" FROM error_feedback INNER JOIN user where error_feedback.`uid`=user.`uid` and user.`uid`='.$uid.' UNION SELECT fid,user.`uid`,username,profile_photo,"null" AS "phone_model",description,contact,add_time,progress,img_path,"1" as "what" FROM feature_suggestions INNER JOIN user where feature_suggestions.`uid`=user.`uid` and user.`uid`='.$uid.' order by add_time desc;');
}
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

