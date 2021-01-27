<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$json_string = file_get_contents('php://input');
$body = json_decode($json_string,true);
$cid = $body['cid'];
$correct_times = $body["correct_times"];
$error_times = $body["error_times"];
$what = $body["what"];
//date_default_timezone_set('prc');
//$last_date = date('Y-m-d',time());
if($what==0){
	mysqli_query($conn,'UPDATE collect SET correct_times='.$correct_times.',error_times='.$error_times.' WHERE cid='.$cid);
}else{	
	date_default_timezone_set('prc');
	$last_date = date('Y-m-d',time());
	if($correct_times >= 6){//已掌握
		$review_date = "1970-01-01";
	}else{	
		$add_day = array(1,2,4,7,15); 
		$review_date = date('Y-m-d',strtotime("+".$add_day[$correct_times-1]." day"));
	}
	mysqli_query($conn,'UPDATE collect SET correct_times='.$correct_times.',error_times='.$error_times.',last_date="'.$last_date.'",review_date="'.$review_date.'" WHERE cid='.$cid);
}

echo "更新成功";
//5.关闭连接
mysqli_close($conn);
?>

