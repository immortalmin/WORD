<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$json_string = file_get_contents('php://input');
$body = json_decode($json_string,true);
$uid = $body['uid'];
//$username = $body['username'];
//$motto = $body['motto'];
//$last_login = $body['last_login'];
if(array_key_exists('username',$body)){
	mysqli_query($conn,'UPDATE user SET username="'.$body['username'].'" WHERE uid='.$uid);
}
if(array_key_exists('motto',$body)){
	mysqli_query($conn,'UPDATE user SET motto="'.$body['motto'].'" WHERE uid='.$uid);
}
if(array_key_exists('last_login',$body)){
	mysqli_query($conn,'UPDATE user SET last_login='.$body['last_login'].' WHERE uid='.$uid);
}
if(array_key_exists('telephone',$body)){
	mysqli_query($conn,'UPDATE user SET telephone="'.$body['telephone'].'" WHERE uid='.$uid);
}
//5.关闭连接
mysqli_close($conn);
?>

