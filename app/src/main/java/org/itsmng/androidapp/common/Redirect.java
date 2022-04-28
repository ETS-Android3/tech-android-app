package org.itsmng.androidapp.common;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Redirect {

    /**
     * Redirect to a target fragment
     *
     * @param targetFragment : Target fragment
     * @param fragmentManager : Fragment manager
     * @param hostFragmentId : Nav Host fragment ID
     */
    public static void redirectToAnotherFragment(Fragment targetFragment, FragmentManager fragmentManager, int hostFragmentId){
        redirectToAnotherFragmentWithArgs(targetFragment, fragmentManager, hostFragmentId, null);
    }

    /**
     * Redirect to a target fragment
     *
     * @param targetFragment : Target fragment
     * @param fragmentManager : Fragment manager
     * @param hostFragmentId : Nav Host fragment ID
     * @param arguments : Arguments Bundle to pass data to targetFragment
     */
    public static void redirectToAnotherFragmentWithArgs(Fragment targetFragment, FragmentManager fragmentManager, int hostFragmentId, Bundle arguments){
        if(arguments != null){
            targetFragment.setArguments(arguments);
        }

        assert fragmentManager != null;
        fragmentManager.beginTransaction()
                .replace(hostFragmentId, targetFragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

}
