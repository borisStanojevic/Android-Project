package com.example.student.myproject.fragments;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.myproject.PostsActivity;
import com.example.student.myproject.R;
import com.example.student.myproject.ReadPostActivity;
import com.example.student.myproject.SettingsActivity;
import com.example.student.myproject.UserActivity;
import com.example.student.myproject.UsersActivity;
import com.example.student.myproject.dialogs.UpdatePostDialog;
import com.example.student.myproject.dialogs.UserDialog;
import com.example.student.myproject.model.Post;
import com.example.student.myproject.model.Tag;
import com.example.student.myproject.model.User;
import com.example.student.myproject.util.PostService;
import com.example.student.myproject.util.UserService;
import com.example.student.myproject.util.Util;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostFragment extends Fragment {

    private Post post;
    private String imagePath;
    private ImageButton chooseImageBtn;

    public PostFragment(){
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == getActivity().RESULT_OK)
        {
            if(data == null)
            {
                Toast.makeText(getActivity(), "Unable to choose image", Toast.LENGTH_SHORT).show();
                return;
            }
            Uri imageUri = data.getData();
            imagePath = getRealPathFromUri(imageUri);
            File file = new File(imagePath);
            String extension = file.getName().split("\\.")[1];

            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", post.getId() + "." + extension , requestBody);

            PostService postService = Util.retrofit.create(PostService.class);
            Call<Post> call = postService.uploadPhoto(filePart);
            call.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, final Response<Post> response)
                {
                    Intent i = new Intent(getActivity(), PostsActivity.class);
                    startActivity(i);
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t)
                {
                    Toast.makeText(getActivity(), "Failed to upload photo : " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private  String getRealPathFromUri(Uri uri)
    {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(getActivity().getApplicationContext(), uri, projection, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(columnIndex);
        cursor.close();
        return  result;
    }

    public void fillLayout(final Post post)
    {
        ImageView ivPhoto = (ImageView) getView().findViewById(R.id.iv_post_photo);
        String url = Util.SERVICE_API_PATH + "images/" + post.getPhoto();
        Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(ivPhoto);
        TextView tvTitle = (TextView) getView().findViewById(R.id.tv_post_title);
        tvTitle.setText(post.getTitle());
        TextView tvContent = (TextView) getView().findViewById(R.id.tv_post_content);
        tvContent.setText(post.getContent());
        TextView tvPostAuthor = (TextView) getView().findViewById(R.id.tv_post_author);
        tvPostAuthor.setText(post.getAuthor().getUsername());

        ImageView ivPostAuthorPhoto = (ImageView) getView().findViewById(R.id.iv_post_author_photo);
        String url2 = Util.SERVICE_API_PATH + "images/" + post.getAuthor().getPhoto();
        Picasso.get().load(url2).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(ivPostAuthorPhoto);

        TextView tvDatePosted = (TextView) getView().findViewById(R.id.tv_date_posted);
        tvDatePosted.setText(post.getDate());
        TextView tvTags = (TextView) getView().findViewById(R.id.tv_post_tags);
        tvTags.setVisibility(View.VISIBLE);
        final TextView tvPostLikes = (TextView) getView().findViewById(R.id.tv_post_likes);
        tvPostLikes.setText(String.valueOf(post.getLikes()) + " ");
        final TextView tvPostDislikes = (TextView) getView().findViewById(R.id.tv_post_dislikes);
        tvPostDislikes.setText(" " + String.valueOf(post.getDislikes()));
        Geocoder geocoder = new Geocoder(getView().getContext());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(post.getLocationLatitude(), post.getLocationLongitude(), 1);
            String locality = addresses.get(0).getLocality();
            String countryName = addresses.get(0).getCountryName();
            ((TextView) getView().findViewById(R.id.tv_location_posted)).setText("Near " + locality + " , " + countryName);
        } catch (IOException exc) {
            exc.printStackTrace();
        } catch (IndexOutOfBoundsException exc) {
            ((TextView) getView().findViewById(R.id.tv_location_posted)).setText("Unknown");
        }
            for (Tag tag : post.getTags()) {
                tvTags.append(tag.getName() + ";");
            }
            if (tvTags.getText() == null || "".equals(tvTags.getText()))
                tvTags.setVisibility(View.GONE);
            else
                tvTags.setText(tvTags.getText().subSequence(0, tvTags.getText().length() - 1));


        final ImageButton btnLike = (ImageButton)getView().findViewById(R.id.btn_like_post);
        final ImageButton btnDislike = (ImageButton)getView().findViewById(R.id.btn_dislike_post);
        btnLike.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //Ako ulogovani nije autor posta ne moze like
                if(getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("loggedInUserUsername", null)
                    .equals(post.getAuthor().getUsername()))
                {
                    Snackbar.make(getView(), "You can not like your own posts", Snackbar.LENGTH_LONG).show();
                    return;
                }
                //U suprotnom uvecaj like za 1, umanji dislike za 1 ako je pozitivan, onemoguci like dugme, omoguci dislajk
                int currentLikes = Integer.parseInt(tvPostLikes.getText().toString().trim());
                int newLikes = currentLikes + 1;
                int currentDislikes = Integer.parseInt(tvPostDislikes.getText().toString().trim());
                int newDislikes = currentDislikes > 0 ? currentDislikes - 1 : 0;
                post.setLikes(newLikes);
                post.setDislikes(newDislikes);
                tvPostLikes.setText(String.valueOf(newLikes) + " ");
                tvPostDislikes.setText(" " + String.valueOf(newDislikes));
                btnLike.setClickable(false);
                btnDislike.setClickable(true);
            }
        });

        btnDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Ako ulogovani nije autor posta ne moze dislike
                if(getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("loggedInUserUsername", null)
                        .equals(post.getAuthor().getUsername()))
                {
                    Snackbar.make(getView(), "You can not dislike your own posts", Snackbar.LENGTH_LONG).show();
                    return;
                }
                //U suprotnom uvecaj dislike za 1, umanji like za 1 ako je pozitivan, onemoguci dislike, omoguci lajk
                int currentDislikes = Integer.parseInt(tvPostDislikes.getText().toString().trim());
                int newDislikes = currentDislikes + 1;
                int currentLikes = Integer.parseInt(tvPostLikes.getText().toString().trim());
                int newLikes = currentLikes > 0 ? currentLikes - 1 : 0;
                post.setLikes(newLikes);
                post.setDislikes(newDislikes);
                tvPostDislikes.setText(" " + String.valueOf(newDislikes));
                tvPostLikes.setText(String.valueOf(newLikes) + " ");
                btnLike.setClickable(true);
                btnDislike.setClickable(false);
            }
        });

        chooseImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        chooseImageBtn = (ImageButton)  view.findViewById(R.id.btn_upload_photo);
        return view;
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        //Preuzimam id posta poslat iz ReadPostActivity
        Bundle bundle = getArguments();
        int id = bundle.getInt("id");

        //Radim GET zahtjev serveru za post ciji je id 'id'
        PostService postService = Util.retrofit.create(PostService.class);
        final Call<Post> call = postService.getById(id);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response)
            {
                post = response.body();
                if(post != null)
                    fillLayout(post);
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t)
            {
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemClickedId = item.getItemId();
        switch (itemClickedId) {
            case R.id.action_delete:
                if(getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("loggedInUserUsername", null) != null
                        && !(getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("loggedInUserUsername", null).equals(post.getAuthor().getUsername())))
                {
                    Snackbar.make(getView(),"You are not the author of this post",Snackbar.LENGTH_LONG).show();
                    break;
                }
                else {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    PostService postService = Util.retrofit.create(PostService.class);
                                    final Call<Void> call = postService.delete(post.getId());
                                    call.enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            Intent intent = new Intent(getActivity().getApplicationContext(), PostsActivity.class);
                                            startActivity(intent);
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                    break;
                }
            case R.id.action_update:
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                UpdatePostDialog updatePostDialog = new UpdatePostDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable("post", post);
                updatePostDialog.setArguments(bundle);
                updatePostDialog.show(fragmentManager, "Post");
                break;
            case R.id.action_settings:
                Intent intent = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public Post getPost() {
        return post;
    }


}
