package Dynamics;
import Bus.Bus;


import Dynamics.EnergyLoss;


/**
 * Created by ral2bp on 2016.09.29..
 */
public class VehicleDynamics {
    Bus bus;
    private static VehicleDynamics instance;

    public static VehicleDynamics GetInstance(){
        if(instance==null){
            instance=new VehicleDynamics();
        }
        return instance;
    }

    private VehicleDynamics(){

    }

    private final float GEAR_D_ACCELERATION_COEFFICIENT = 0.8f;
    private final float GEAR_R_ACCELERATION_COEFFICIENT = 1.5f;
    private final float GEAR_P_ACCELERATION_COEFFICIENT = 1.2f;
    private final float GEAR_N_ACCELERATION_COEFFICIENT = 0f;

    private final float GEAR_D_MAX_SPEED = 50f;
    private final float GEAR_R_MAX_SPEED = 8f;
    private final float GEAR_P_MAX_SPEED = 4f;
    private final float GEAR_N_MAX_SPEED = 0f;

    // This is 7 meters per second speed reduction in a second at full brake
    private final float BRAKE_PEDAL_DECELERATION_COEFFICIENT = 7f;

    // This is the decleration of the car body, when the car is not driven and the brake pedal is not pressed
    private final float STATIC_DECELERATION_COEFFICIENT = 0.2f;

    private float getGearAcceleration() {
        Bus.GearPosition gear = bus.getGearPosition();
        if (gear == Bus.GearPosition.NEUTRAL) {
            return GEAR_N_ACCELERATION_COEFFICIENT;
        }

        if (gear == Bus.GearPosition.REVERSE) {
            return GEAR_R_ACCELERATION_COEFFICIENT;
        }

        if (gear == Bus.GearPosition.PARK) {
            return GEAR_P_ACCELERATION_COEFFICIENT;
        }

        if (gear == Bus.GearPosition.DRIVE) {
            return GEAR_D_ACCELERATION_COEFFICIENT;
        }

        return 0;
    }


    private float calculateGaspedalAcceleration() {
        return bus.getGasPedal() / 100f;
    }


    private float calculateBrakepedalDeceleration() {
        return bus.getBrakePedal() / 100f * BRAKE_PEDAL_DECELERATION_COEFFICIENT;
    }


    private float calculateEngineBrakeDeceleration() {
        float gearacceleration = this.getGearAcceleration();
        return gearacceleration / 4;
    }


    private float calculateAcceleration() {
        float gearacceleration = this.getGearAcceleration();
        float gaspedalacceleration = this.calculateGaspedalAcceleration();
        float enginebrakedecleration = this.calculateEngineBrakeDeceleration();
        float brakepedaldeceleration = this.calculateBrakepedalDeceleration();
        float staticdecleration = STATIC_DECELERATION_COEFFICIENT;

        float gasacceleration = gaspedalacceleration * gearacceleration;
        float decelerationsum = enginebrakedecleration + brakepedaldeceleration + staticdecleration;

        float accelerationsum = gasacceleration - decelerationsum;// - this.acceleration;
        return accelerationsum;
    }


    private float getGearMaxSpeed() {
        Bus.GearPosition gear = bus.getGearPosition();
        if (gear == Bus.GearPosition.NEUTRAL) {
            return GEAR_N_MAX_SPEED;
        }

        if (gear == Bus.GearPosition.REVERSE) {
            return GEAR_R_MAX_SPEED;
        }

        if (gear == Bus.GearPosition.PARK) {
            return GEAR_P_MAX_SPEED;
        }

        if (gear == Bus.GearPosition.DRIVE) {
            return GEAR_D_MAX_SPEED;
        }

        return 0;
    }


    private void updateAcceleration() {
        float newacceleration = this.calculateAcceleration();
        bus.setAcceleration((int)newacceleration);
    }


    private void updateSpeed() {
        float gearmaxspeed = this.getGearMaxSpeed();
        float currentspeed = bus.getCurrentSISpeed();
        float currentacc = bus.getAcceleration();
        float newspeed = Math.max(0, Math.min(currentspeed + currentacc, gearmaxspeed));

        if (bus.getGearPosition() == Bus.GearPosition.REVERSE) {
            newspeed = -newspeed;
        }

        bus.setCurrentSISpeed(newspeed);
    }

    public void update() {
        this.updateAcceleration();
        this.updateSpeed();
    }
}
