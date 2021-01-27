<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$username = $_POST['username'];
$pwd = $_POST['pwd'];
//$telephone = $_POST['telephone'];
//$email = $_POST['email'];
$last_login = time()*1000;
mysqli_query($conn,'INSERT INTO user(username,pwd,last_login) VALUES ("'.$username.'","'.$pwd.'","'.$last_login.'")');
$uid = mysqli_insert_id($conn);
mysqli_query($conn,'INSERT INTO setting(uid) value ('.$uid.')');
//mysqli_query($conn,'INSERT INTO recite(uid,wid) SELECT '.$uid.',words.`wid` FROM words;');
$img_flag = $_POST['img_flag'];
if($img_flag=="1"){
	if(empty($_FILES)){
        	echo "无上传文件";
	}else{
        	$dirPath = '../img/profile/';
        	if(!is_dir($dirPath)){
                	//echo "创建目录";
                	@mkdir($dirPath);
        	}
        	$file = $_FILES['image'];
        	if(is_uploaded_file($file['tmp_name'])){
                	$info = pathinfo($_FILES['image']['name']);
                	$ext = $info['extension'];
                	if(move_uploaded_file($_FILES['image']['tmp_name'],$dirPath.$uid.'.'.$ext)){
                        	mysqli_query($conn,'UPDATE user SET profile_photo="'.$uid.'.'.$ext.'" WHERE uid='.$uid);
                        	//echo "头像上传成功";
                	}else{
                        	echo "头像上传失败";
                	}

        	}else{
                	echo "头像图片不合法";
        	}

	}
}

$result = mysqli_query($conn,"SELECT * FROM user WHERE uid=".$uid);
$output = array();
while($data = mysqli_fetch_array($result,MYSQLI_ASSOC)){
	$output[] = $data;
}
if($output){
	echo json_encode($output,JSON_UNESCAPED_UNICODE);
}else{
	echo json_encode([],JSON_UNESCAPED_UNICODE);
}
mysqli_free_result($result);
mysqli_close($conn);
?>

