package mad4124.team_sundry.task.ui.maps;

import static mad4124.team_sundry.task.ui.task.TaskListFragment.CATEGORY_ID;
import static mad4124.team_sundry.task.ui.task.TaskListFragment.IsShowAllMap;
import static mad4124.team_sundry.task.ui.task.TaskListFragment.TASK_ID;
import static mad4124.team_sundry.task.ui.taskDetail.TaskDetailFragment.SELECTED_LOCATION;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;
import mad4124.team_sundry.task.R;
import mad4124.team_sundry.task.databinding.FragmentMapAllTasksBinding;
import mad4124.team_sundry.task.model.MapLocation;
import mad4124.team_sundry.task.model.Task;
import mad4124.team_sundry.task.ui.MainViewModel;

@AndroidEntryPoint
public class MapAllTasksFragment extends Fragment implements OnMapReadyCallback {

    //GG Map
    private GoogleMap mMap;

    //Inside logic
    private Location currentLocation;
    private Boolean isEditingMode = false;

    List<MapLocation> locations = new ArrayList(); //send this if wanna show tasks locations (Optional)
    Boolean isShowAllMap = false; // send this if it show all map or pick loc map (Required)
    MapLocation selectedLocationObj; //send this if edit (Optional), get this when finish selected (Optional)
    MainViewModel viewModel;

    // location with location manager and listener
    private static final int REQUEST_CODE = 1;
    private LocationManager locationManager;
    private LocationListener locationListener;

    //Binding
    private FragmentMapAllTasksBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapAllTasksBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);

        //Search
        // Initialize the AutocompleteSupportFragment.

        if (!Places.isInitialized()) {
            Places.initialize(getContext(), getString(R.string.google_maps_key));
        }
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.getView().setBackgroundColor(Color.WHITE);
        autocompleteFragment.setPlaceFields(Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("0", "Place: " + place.getName() + ", " + place.getId());
                setMarker(place.getLatLng(), place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("0", "An error occurred: " + status);
            }
        });

        //obtain bundle
        long categoryID = getArguments().getLong(CATEGORY_ID,-1);
        long taskId = getArguments().getLong(TASK_ID,-1);
        MapLocation mapLocation = (MapLocation) getArguments().getSerializable(SELECTED_LOCATION);
        isShowAllMap =  getArguments().getBoolean(IsShowAllMap, false);

        if (categoryID != -1) {
            locations = viewModel.getAllMapPin(categoryID);
        } else if (taskId != -1) {
            isEditingMode = true;
            selectedLocationObj = viewModel.getMapPin(taskId);
        }else if (mapLocation != null){
            selectedLocationObj = mapLocation;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (currentLocation == null && !isShowAllMap && selectedLocationObj == null) { //pick map
                    currentLocation = location;
                    //show set current location.
                    try {
                        setCurrentLocationMarker();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (selectedLocationObj != null && !isShowAllMap && currentLocation == null) { //edit map
                    currentLocation = location;
                    setMarker(new LatLng(selectedLocationObj.getLat(), selectedLocationObj.getLng()), selectedLocationObj.getName());
                }
            }
            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };

        if (!hasLocationPermission())
            requestLocationPermission();
        else
            startUpdateLocation();

        if (isShowAllMap) {
            //no search on show all tasks
            getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment).getView().setVisibility(View.GONE);
            binding.currentLocBtn.setVisibility(View.GONE);

            //show all markers
            if (locations.size() > 0) {
                for (MapLocation location : locations) {
                    setMarker(new LatLng(location.getLat(), location.getLng()), location.getName());
                }
            }
        } else {
            //pick location
            //show search
            getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment).getView().setVisibility(View.VISIBLE);
            binding.currentLocBtn.setVisibility(View.VISIBLE);

            // apply long press gesture
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(@NonNull LatLng latLng) {
                    try {
                        setMarker(latLng, getAddress(latLng));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            binding.currentLocBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        setCurrentLocationMarker();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void setMarker(LatLng latLng, String title) {
        if (!isShowAllMap) {
            mMap.clear();
            setSelectLocation(latLng, title);
        }

        MarkerOptions options = new MarkerOptions().position(latLng)
                .title(title);
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    private void setCurrentLocationMarker() throws IOException {
        if (currentLocation != null) {
            mMap.clear();
            LatLng userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            MarkerOptions options = new MarkerOptions().position(userLocation)
                    .title("You are here")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .snippet("Your Location");
            mMap.addMarker(options);
            setSelectLocation(userLocation, getAddress(userLocation));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        }
    }

    private void setSelectLocation(LatLng latLng, String title) {
        selectedLocationObj = new MapLocation();
        selectedLocationObj.setLat(latLng.latitude);
        selectedLocationObj.setLng(latLng.longitude);
        selectedLocationObj.setName(title);
    }

    private String getAddress(LatLng latLng) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        return address;
    }

    private void startUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
    }

    private void requestLocationPermission() {
        requestPermissions(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
//        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_CODE == requestCode) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startUpdateLocation();
            }
        }
    }

    @Override
    public void onDestroyView() {
        saveMap();
        super.onDestroyView();
    }

    private void saveMap() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("map",selectedLocationObj);
        getParentFragmentManager().setFragmentResult("requestKey",bundle);
//        if (selectedLocationObj != null) {
//            if (isEditingMode) {
//                Toast.makeText(getActivity(), "Updated Map", Toast.LENGTH_SHORT).show();
//                viewModel.updateMap(selectedLocationObj);
//            } else {
//                Toast.makeText(getActivity(), "Saved Map", Toast.LENGTH_SHORT).show();
//                viewModel.insertMap(selectedLocationObj);
//            }
//        }
    }
}
