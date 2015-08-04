package com.star.to_dolist;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private List<String> mToDoList;
    private ArrayAdapter<String> mArrayAdapter;
    private EditText mEditText;
    private Button mAddButton;

    private TextToSpeech mTextToSpeech;
    private boolean mSpeechReady = false;

    public static final String FILE_NAME = "ToDoList.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.to_do_list);

        mToDoList = new ArrayList<>();

        mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mToDoList);

        mListView.setAdapter(mArrayAdapter);

        mEditText = (EditText) findViewById(R.id.edit_text);

        mAddButton = (Button) findViewById(R.id.add_button);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mEditText.getText().toString()) && (mToDoList != null)) {
                    mToDoList.add(mEditText.getText().toString());
                    mEditText.setText("");
                    mArrayAdapter.notifyDataSetChanged();
                }
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mToDoList.remove(position);
                mArrayAdapter.notifyDataSetChanged();
                return true;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleClick(position);
            }
        });

        mTextToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                mSpeechReady = true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mToDoList != null) {
            try {
                Scanner scanner = new Scanner(openFileInput(FILE_NAME));
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    mToDoList.add(line);
                }
                scanner.close();
                mArrayAdapter.notifyDataSetChanged();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            PrintStream printStream = new PrintStream(openFileOutput(FILE_NAME, MODE_PRIVATE));
            for (int i = 0; i < mToDoList.size(); i++) {
                printStream.println(mToDoList.get(i));
            }
            printStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {

        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }

        super.onDestroy();
    }

    private void handleClick(final int position) {
        if (mSpeechReady) {
            mTextToSpeech.speak(mToDoList.get(position), TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
