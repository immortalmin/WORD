<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$json_string = file_get_contents('php://input');
$body = json_decode($json_string,true);
$id = $body['id'];
$collect = $body["collect"];
mysqli_query($conn,'UPDATE word_table SET collect='.$collect.' WHERE wid='.$id);
//5.关闭连接
mysqli_close($conn);
?>

