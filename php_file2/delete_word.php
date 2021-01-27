<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$json_string = file_get_contents('php://input');
$body = json_decode($json_string,true);
$wid = $body['wid'];
mysqli_query($conn,'DELETE FROM example WHERE wid='.$wid);
mysqli_query($conn,'DELETE FROM collect WHERE wid='.$wid);
mysqli_query($conn,'DELETE FROM words WHERE wid='.$wid);
//5.关闭连接
mysqli_close($conn);
?>

