<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 

$link=mysqli_connect("서버IP주소","사용자이름","패스워드","DB이름");  //보안상 주석처리
if (!$link)  
{ 
   echo "MySQL 접속 에러 : ";
   echo mysqli_connect_error();
   exit();
}  


mysqli_set_charset($link,"utf8");  

//POST 값을 읽어옴
$voice=isset($_POST['voice']) ? $_POST['voice'] : '';  
$message=isset($_POST['message']) ? $_POST['message'] : '';  

if ($voice !="" and $message !="" ){   
  
    $sql="insert into alarm_inform(id,voice,message) values(NULL,'$voice','$message')";  
    $result=mysqli_query($link,$sql);  

    if($result){  
       echo "SQL문 처리 성공";  
    }  
    else{  
       echo "SQL문 처리중 에러 발생 : "; 
       echo mysqli_error($link);
    } 
 
} else {
    echo "데이터를 입력하세요 ";
}


mysqli_close($link);
?>

<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){
?>

<html>
   <body>
   
      <form action="<?php $_PHP_SELF ?>" method="POST">
         Voice: <input type = "text" name = "voice" />
         Message: <input type = "text" name = "message" />
         <input type = "submit" />
      </form>
   
   </body>
</html>
<?php
}
?>
