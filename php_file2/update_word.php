<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$json_string = file_get_contents('php://input');
$body = json_decode($json_string,true);
$id = $body['id'];
$word_group = $body["word_group"];
$C_meaning = $body["C_meaning"];
$page = $body["page"];
mysqli_query($conn,'UPDATE word_table SET word_group="'.$word_group.'",C_meaning="'.$C_meaning.'",page="'.$page.'" WHERE wid='.$id);

echo "更新成功";
//5.关闭连接
mysqli_close($conn);
?>

