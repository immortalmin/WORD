<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$json_string = file_get_contents('php://input');
$body = json_decode($json_string,true);
$id = $body['id'];
$correct_times = $body["correct_times"];
$error_times = $body["error_times"];
$prof_flag = $body["prof_flag"];
date_default_timezone_set('prc');
$last_date = date('Y-m-d',time());
mysqli_query($conn,'UPDATE recite_table SET correct_times='.$correct_times.',error_times='.$error_times.',prof_flag='.$prof_flag.',last_date="'.$last_date.'" WHERE wid='.$id);

echo "更新成功";
//5.关闭连接
mysqli_close($conn);
?>

