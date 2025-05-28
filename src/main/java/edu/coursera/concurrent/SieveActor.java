package edu.coursera.concurrent;

import edu.rice.pcdp.Actor;
import static edu.rice.pcdp.PCDP.finish; // Add this import at the top

/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 *
 * TODO Fill in the empty SieveActorActor actor class below and use it from
 * countPrimes to determin the number of primes <= limit.
 */
public final class SieveActor extends Sieve {
    /**
     * {@inheritDoc}
     *
     * TODO Use the SieveActorActor class to calculate the number of primes <=
     * limit in parallel. You might consider how you can model the Sieve of
     * Eratosthenes as a pipeline of actors, each corresponding to a single
     * prime number.
     */
  

    @Override
    public int countPrimes(final int limit) {
        SieveActorActor sieveActor = new SieveActorActor(2);
        finish(() -> { // Wrap sends in a finish block
            for (int i = 2; i <= limit; i++) {
                sieveActor.send(i);
            }
            sieveActor.send(0);
        });

        int count = 0;
        SieveActorActor actor = sieveActor;
        while (actor != null) {
            count++;
            System.out.println(actor.getlocalPrime());
            actor = actor.nextActor;
            
        }
        return count;
    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {
        /**
         * Process a single message sent to this actor.
         *
         * TODO complete this method.
         *
         * @param msg Received message
         */
        private SieveActorActor nextActor;
        
        private int localPrime;
        public SieveActorActor(final int localPrime) {
            // Initialize any necessary state here
            nextActor = null; 
            this.localPrime = localPrime;           
        }

        public int getlocalPrime() {
            return localPrime;
        }

        @Override
        public void process(final Object msg) {
            int candidate = (int) msg;
            if (candidate == 0) {                
                if (nextActor != null) {
                    nextActor.send(candidate);
                }
            } else {
                
                if (candidate % localPrime != 0) {                                        
                    if (nextActor != null) {
                        nextActor.send(candidate);
                    }else {
                        nextActor = new SieveActorActor(candidate);
                        nextActor.send(candidate);                        
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SieveActor sieve = new SieveActor();
        int limit = 100;
        int primeCount = sieve.countPrimes(limit);
        System.out.println("Number of primes <= " + limit + ": " + primeCount);
    }
}
