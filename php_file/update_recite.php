<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$id = $_GET["id"];
$correct_times = $_GET["correct_times"];
$error_times = $_GET["error_times"];
$prof_flag = $_GET["prof_flag"];
mysql_query("UPDATE recite_words SET correct_times=".$correct_times.", error_times=".$error_times.", prof_flag=".$prof_flag." WHERE id =".$id);

echo "更新成功";
//5.关闭连接
mysql_close($conn);
?>

