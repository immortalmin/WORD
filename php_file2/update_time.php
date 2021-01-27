<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$json_string = file_get_contents('php://input');
$body = json_decode($json_string,true);
$uid = $body['uid'];
$udate = $body['udate'];
$utime = $body['utime'];
$utimestamp = $body['utimestamp'];
mysqli_query($conn,'INSERT INTO usetime(uid,udate,utime) VALUES ('.$uid.',"'.$udate.'",'.$utime.');');
if(!empty($utimestamp)){
	mysqli_query($conn,'UPDATE user SET last_login='.$utimestamp.' where uid='.$uid);
}
//5.关闭连接
mysqli_close($conn);
?>

