#### 2023 KAIST MadCamp Week1
###### 이화여자대학교 컴퓨터공학과 황재령, KAIST 산업 및 시스템공학과 손다윤
# Pro . dev
## *DevDiary*
### 개발자들의 프로젝트 관리를 위한 Platform
## 프로젝트 소개

![main_img](https://user-images.githubusercontent.com/37971925/148037772-2f25ff79-0f50-4652-a902-6f33ca711cc1.png)

## 과제 소개
이번 1주차 과제는 다음과 같이 3개의 탭으로 구성된 어플리케이션을 개발하는 것이다.
3개 탭의 내용은 아래와 같다.
>
* 연락처를 보여주는 화면
* 20장 내외의 이미지로 구성된 이미지 갤러리를 보여주는 화면
* 자유롭게 구현한 기능을 보여주는 화면
<br>

### 1번탭
***
#### 1) QR 코드를 통해 사용자 정보 관리
자신과 협업하여 개발 프로젝트를 하는 팀원의 정보를 저장하기 위한 탭

JSON 데이터를 효과적으로 다룰 수 있는 라이브러리를 찾다가
<U>[ZXing](https://github.com/zxing/zxing)</U> 라이브러리를 사용하여 연락처 데이터를 추가하고 편집할 수 있게 진행했다.

입력창에 입력한 정보(이름, 학교, 연락처, 깃허브 주소, 이메일)를 QR 코드에 저장 후 정보를 담아 QR 코드를 생성할 수 있고 다른 사람의 QR 코드를 읽어올 수 있으며, Activity의 경우 onActivityResult() 함수를 Override하여 결과 값을 받아올 수 있다.
QR 코드를 인식하는 즉시 사용자의 연락처 목록에 자동으로 업데이트 되도록 구현했다.


*QR코드 정보입력.gif*
<br>
정보 입력 fragment에는 사진도 업로드 할 수가 있는데 디바이스 갤러리와 연동해서 사진을 골라넣고 저장할 수 있도록 하였다.

아래는 QR 코드 정보 저장, QR 코드 생성, QR 코드 인식, 인식한 정보 자동 저장을 지정하는 맵의 코드 중 일부를 발췌했다. 

```java //MyProfileFragment.java
private Bitmap generateQRCodeBitmap(String content) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 400, 400);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }

// PhoneBook.java
public void startQRScanner() {
        Log.d("PhoneBook", "startQRScanner called");
        IntentIntegrator integrator = new IntentIntegrator(getActivity()).forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR code");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        Log.d("PhoneBook", "Initiating scan");
        integrator.initiateScan();
    }
```


*QR코드 생성화면.gif*
<br>

*QR코드 인식화면.gif*
<br>

*인식 후 리스트에 추가된 모습*
<br>


그리고 연락처는 프로필 형식과 리스트 형식 중 고민하다가 탭 기본 화면에는 가독성과 편집이 쉬운 리스트의 형태로 보여주며,
리스트 프로필을 클릭했을 때는 프로필 형식의 dialog 카드는 띄울 수 있도록 하였다. 이를 위해 프로필 카드를 직접 디자인했다.

![](https://user-images.githubusercontent.com/37971925/148042318-4d0216e8-b154-4391-b7ec-22f966754810.gif)

*첫번째 탭 메인 화면 작동 예시.gif*
<br>

리스트 검색 기능은 UI를 위해 따로 검색 버튼을 클릭하지 않아도 검색창에 입력하면 바로 찾아서 검색되는 기능을 구현했다.


그리고 프로필 카드의 추가 및 삭제를 위한 dialog 창도 직접 디자인해서 연결했다.
![그림1](https://user-images.githubusercontent.com/37971925/148050280-b86e7919-50e3-4f4d-86d8-7e2aa3f525db.png)*삭제화면 예시. 프로필 카드를 추가하는 화면도 있지만, 안드로이드 에뮬레이터에서는 QR 코드 인식이 불가능해 따로 첨부하지 못했다.*

이를 위해 별도의 XML 파일을 만들어 사용했으며, 삭제 시 모달 창을 띄우고 결과에 따라 프로필 카드를 삭제하는 코드를 작성했다.

```java
deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performDeletion(contact);
                alertDialog.dismiss();
            }
        });
```
*프로필 카드의 삭제 버튼에 onClickListener를 활용하여 삭제 기능 실행*
<br>

- 추가로 프로필 카드의 연락처를 클릭하면 전화 연결 확인 모달 창이 뜨고, 전화 연결 <br>
- 깃허브 링크를 클릭하면 해당 팀원의 깃허브 홈으로 이동
- 상대의 이메일 클릭하면 이메일 작성 창으로 바로 이동(이메일 보내기 가능)

### 2번탭
***
두번째 탭은 개인 기록용 sns 형식의 화면이다. 우리는 개발 기록용이므로, 협업 팀원들이 올린 게시물과 메모를 보여줄 수 있는 화면으로 구성했다.
즉 협업 팀원의 개발 진행 상황을 별도의 연락없이 계속해서 확인이 가능하고, 코드리뷰를 할 수 있도록 만들었다.

화면의 구성은 인스타그램이나 페이스북 등을 참고해서 배치했다. 사진의 경우 GridView를 사용하여 한 열에 2개의 사진이 배치 될 수 있게 했다.

![두번째 탭](https://user-images.githubusercontent.com/37971925/148053534-aade4c37-67cd-419d-9c17-435728f30c57.png)

사진은 json 파일로 하려다가 sns의 취지를 살리기 위해 디바이스의 사진첩과 연동하여 해당하는 그리드에 등록되도록 구성했다. 
```java
private void registerImagePickerLauncher() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        try {
                            // Convert Uri to Bitmap
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), result);

                            // Save the bitmap to internal storage and get the path
                            String imagePath = InternalStorageUtil.saveToInternalStorage(requireContext(), bitmap);

                            // Add the image path to the view model
                            viewModel.addImagePath(imagePath);
                            imageAdapter.notifyDataSetChanged();

                            // Save updated paths to SharedPreferences
                            viewModel.saveImagePaths(requireContext());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
```

사진 클릭 시 게시글을 볼 수 있는 Fragment를 실행한다. 
이때 Fragment를 전환하면서 애니메이션을 추가했는데, 아래와 같은 코드로 Fragment 간의 전환에 애니메이션을 실행할 수 있다.

```XML
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android">
    <alpha
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:interpolator="@android:anim/accelerate_interpolator"
        android:fromAlpha="0.0"
        android:toAlpha="1.0"
        android:duration="500"/>
</set>
```
<br>
그리고 등록된 사진은 게시글로 확인 시 사진 확대/축소, 좌우 회전 기능까지 추가하였다.
등록자의 경우 사진을 회전시키면 메인 화면에도 새롭게 회전된 형태의 이미지가 반영되도록 구현하였다.
<br>
fragment 밑에는 comment를 달 수 있도록 화면을 구성했다. 코멘트를 등록해 간단한 메모 형식으로 자신의 코드 진행 상황을 작성할 수 있다.
메모와 사진 등록 전부 다 동적인 요소이기에 앱을 껐다 키거나 창을 닫았다 열어도 유지되도록 하는 것에 상당 시간을 쏟아 해결하였는데 이미지는 byte 형식으로 바꾸어 map에 저장하여 sharedPreferenced 와 함께 이용했으며 
메모와 해당 사진이 연동되게 사진과 메모에 고유의 id를 부여하여 서로 연동시킨 후 SharedPreferences에 함꼐 저장하였다.


```java
public static ImageDialogFragment newInstance(String imagePath, ArrayList<String> comments) {
        ImageDialogFragment fragment = new ImageDialogFragment();
        Bundle args = new Bundle();
        args.putString("imagePath", imagePath);
        args.putStringArrayList("comments", comments);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null) {
            String imagePath = getArguments().getString("imagePath");
            comments = getArguments().getStringArrayList("comments");

            // Generate a unique key for SharedPreferences based on the image path
            imageKey = PREF_COMMENTS_KEY_PREFIX + imagePath.hashCode();

            sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

            if (savedInstanceState != null) {
                comments = savedInstanceState.getStringArrayList(ARG_COMMENTS);
            } else {
                // 이미지에 대한 메모 불러오기
                comments = loadCommentsFromSharedPreferences(imageKey);
            }
        }
    }
```
<br>


### 3번탭
***
 본 앱은 IT 계열 개발자를 대상으로 만들어졌기 때문에 3번째 탭은 프로젝트 관리형 페이지로 개발자들이 자신의 일정을 관리할 수 있도록 제작하였다.
 마감일 등록과 세부적인 todo-list를 작성하여 체크할 수 있도록 구현하였다.
 
 ![](https://images.velog.io/images/dhwndudkaps2/post/675bd10b-bb7a-4410-b862-ec439c92ff89/image.png)
 
 #### UI
 
 ##### (1) 캘린더
  가장 위에는 캘린더를 등록하였다. 일정 및 마감일이 등록되는 동시에 날짜 하단에 작은 동그라미를 띄워 한 눈에 파악할 수 있도록 제작하였다.

 ![화면_기록_2022-01-04_20_32_07_AdobeCreativeCloudExpress (2)](https://user-images.githubusercontent.com/37971925/148055816-ca214857-0379-41bc-8b8d-ad46881930ad.gif)
##### (2) 마감일 및 세부 일정 등록
하단 editTextView에 일정을 작성하면 마감 시간을 설정할 수 있는 dialog가 뜬다. 시간을 설정한 후 등록하면 귀여운 삭제 버튼과 함께 달력 하단에 일정이 등록된다.
이때 왼쪽에 직접 디자인한 체크 박스를 배치해서 todo기능도 일정과 한꺼번에 관리하기 용이하도록 제작하였다.

 
 #### 코드 구현
 
(1) Project Add Fragment
 #### 체크 박스 개별 생성, 개별 저장
 
```java
private void saveCheckBoxState(CheckBox checkBox, CalendarDay date, int planPosition) {
        // Save checkbox state to SharedPreferences based on the date and plan position
        SharedPreferences preferences = getActivity().getSharedPreferences("CheckBoxStates", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(getCheckBoxKey(date, planPosition), checkBox.isChecked());
        editor.apply();
    }

    private void loadCheckBoxState(CheckBox checkBox, CalendarDay date, int planPosition) {
        // Load checkbox state from SharedPreferences based on the date and plan position
        SharedPreferences preferences = getActivity().getSharedPreferences("CheckBoxStates", Context.MODE_PRIVATE);
        boolean isChecked = preferences.getBoolean(getCheckBoxKey(date, planPosition), false);
        checkBox.setChecked(isChecked);
    }

    private String getCheckBoxKey(CalendarDay date, int planPosition) {
        // Generate a unique key for the checkbox based on the date and plan position
        return "checkbox_" + date.toString() + "_" + planPosition;
    }
```
일정생성과 동시에 등록, 개별 일정과 삭제 등 고유 id부여해서 세 개를 연동하고 앱 껐다 켜도 유지되도록 반영
```java // Create a CheckBox
        CheckBox checkBox = new CheckBox(getActivity());
        checkBox.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        int planPosition = scrollViewLayout.getChildCount();
        // 날짜와 계획 위치를 기반으로 체크박스에 고유 ID 생성
        int uniqueCheckBoxId = generateCheckBoxId(date, planPosition);
        checkBox.setId(uniqueCheckBoxId);
        // Set a custom drawable for the checkbox
        checkBox.setButtonDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.custom_checkbox));
  ```
  

## Libraries
***
* com.journeyapps:zxing-android-embedded:4.1.0  
zxing 라이브러리를 통해 QR 코드를 찍고 생성