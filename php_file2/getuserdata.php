<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$json_string = file_get_contents('php://input');
$body = json_decode($json_string,true);
if(array_key_exists('username',$body)){
	$result =mysqli_query($conn,'SELECT * FROM user WHERE username="'.$body['username'].'"');
}else if(array_key_exists('telephone',$body)){
	$result =mysqli_query($conn,'SELECT * FROM user WHERE telephone="'.$body['telephone'].'"');
}
$output = array();
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

